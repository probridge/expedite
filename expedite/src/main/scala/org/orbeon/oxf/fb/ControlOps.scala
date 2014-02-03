/**
 * Copyright (C) 2011 Orbeon, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.fb

import collection.JavaConverters._
import annotation.tailrec
import org.orbeon.oxf.xml.XMLConstants.XS_STRING_QNAME
import org.orbeon.oxf.xforms.analysis.model.Model
import org.orbeon.oxf.fb.DataModel._
import Model._
import org.orbeon.oxf.xml.{NamespaceMapping, XMLConstants}

import org.orbeon.oxf.xforms.control.XFormsControl
import org.orbeon.saxon.om.{NodeInfo, SequenceIterator}
import org.orbeon.scaxon.XML._
import org.orbeon.oxf.xforms.XFormsConstants._
import org.orbeon.oxf.xforms.action.XFormsAPI._
import collection.mutable
import org.orbeon.saxon.value.StringValue
import org.apache.commons.lang3.StringUtils._
import org.orbeon.oxf.xml.dom4j.Dom4jUtils
import org.orbeon.oxf.fr.FormRunner._
import org.orbeon.oxf.xforms.XFormsUtils._
import org.orbeon.oxf.util.ScalaUtils._

/*
 * Form Builder: operations on controls.
 */
trait ControlOps extends SchemaOps with ResourcesOps {

    self: GridOps ⇒ // funky dependency, to resolve at some point

    val FB = "http://orbeon.org/oxf/xml/form-builder"

    private val MIPsToRewrite = AllMIPs - Type
    private val RewrittenMIPs = MIPsToRewrite map (mip ⇒ mip.name → toQName(FB → ("fb:" + mip.name))) toMap

    private val topLevelBindTemplate: NodeInfo =
        <xf:bind id="fr-form-binds" ref="instance('fr-form-instance')"
                     xmlns:xf="http://www.w3.org/2002/xforms"/>

    private val HelpRefMatcher = """\$form-resources/([^/]+)/help""".r

    // Find control holders (there can be more than one with repeats)
    // Don't return anything if isCustomInstance is true
    def findDataHolders(inDoc: NodeInfo, controlName: String): Seq[NodeInfo] =
        if (! isCustomInstance)
            findBindByName(inDoc, controlName) map { bind ⇒
                // From bind, infer path by looking at ancestor-or-self binds
                val bindRefs = (bind ancestorOrSelf "*:bind" flatMap bindRefOrNodeset).reverse.tail

                val path = bindRefs map ("(" + _ + ")") mkString "/"

                // Assume that namespaces in scope on leaf bind apply to ancestor binds (in theory mappings could be overridden along the way!)
                val namespaces = new NamespaceMapping(Dom4jUtils.getNamespaceContextNoDefault(unwrapElement(bind)))

                // Evaluate path from instance root element
                val instanceElements = eval(formInstanceRoot(inDoc), path, namespaces, null, containingDocument.getRequestStats.addXPathStat)
                instanceElements.asInstanceOf[Seq[NodeInfo]]
            } getOrElse Seq()
        else
            Seq()

    def precedingControlNameInSectionForControl(controlElement: NodeInfo) = {

        val td = controlElement parent * head
        val grid = findAncestorContainers(td).head
        assert(grid.localname == "grid")

        // First check within the current grid as well as the grid itself
        val preceding = td preceding "*:td"
        val precedingInGrid = preceding intersect (grid descendant "*:td")

        val nameInGridOption = precedingInGrid :+ grid flatMap
            { case td if td.localname == "td" ⇒ td \ *; case other ⇒ other } flatMap
                (getControlNameOption(_).toSeq) headOption

        // Return that if found, otherwise find before the current grid
        nameInGridOption orElse precedingControlNameInSectionForGrid(grid, includeSelf = false)
    }

    def precedingControlNameInSectionForGrid(grid: NodeInfo, includeSelf: Boolean) = {

        val precedingOrSelfContainers = (if (includeSelf) Seq(grid) else Seq()) ++ (grid precedingSibling * filter IsContainer)

        // If a container has a name, then use that name, otherwise it must be an unnamed grid so find its last control
        // with a name (there might not be one).
        val controlsWithName =
            precedingOrSelfContainers flatMap {
                case grid if getControlNameOption(grid).isEmpty ⇒ grid \\ "*:td" \ * filter hasName lastOption
                case other ⇒ Some(other)
            }

        // Take the first result
        controlsWithName.headOption flatMap getControlNameOption
    }

    // Ensure that a tree of bind exists
    def ensureBindsByName(inDoc: NodeInfo, name: String) =
        findControlByName(inDoc, name) foreach { control ⇒
            ensureBinds(inDoc, findContainerNames(control) :+ name)
        }

    // Ensure that a tree of bind exists
    def ensureBinds(inDoc: NodeInfo, names: Seq[String]): NodeInfo = {

        // Insert bind container if needed
        val model = findModelElement(inDoc)
        val topLevelBind = findTopLevelBind(inDoc).headOption match {
            case Some(bind) ⇒ bind
            case None ⇒ insert(into = model, after = model \ "*:instance" filter (hasIdValue(_, "fr-form-instance")), origin = topLevelBindTemplate).head
        }

        // Insert a bind into one level
        @tailrec def ensureBind(container: NodeInfo, names: Iterator[String]): NodeInfo = {
            if (names.hasNext) {
                val bindName = names.next()
                val bind =  container \ "*:bind" filter (isBindForName(_, bindName)) match {
                    case Seq(bind: NodeInfo, _*) ⇒ bind
                    case _ ⇒

                        val newBind: Seq[NodeInfo] =
                            <xf:bind id={bindId(bindName)}
                                         ref={annotatedBindRefIfNeeded(bindId(bindName), bindName)}
                                         name={bindName}
                                         xmlns:xf="http://www.w3.org/2002/xforms"/>

                        insert(into = container, after = container \ "*:bind", origin = newBind).head
                }
                ensureBind(bind, names)
            } else
                container
        }

        // Start with top-level
        ensureBind(topLevelBind, names.toIterator)
    }

    // Delete the controls in the given grid cell, if any
    def deleteCellContent(td: NodeInfo) =
        td \ * flatMap controlElementsToDelete foreach (delete(_))

    // Find all associated elements to delete for a given control element
    def controlElementsToDelete(control: NodeInfo): List[NodeInfo] = {

        val doc = control.getDocumentRoot

        // Holders, bind, templates, resources if the control has a name
        val holders = getControlNameOption(control).toList flatMap { controlName ⇒

            val result = mutable.Buffer[NodeInfo]()

            result ++=
                findDataHolders(doc, controlName)             ++=
                findBindByName(doc, controlName)              ++=
                findTemplateHolder(control, controlName)      ++=
                instanceElement(doc, templateId(controlName)) ++=
                findResourceHolders(controlName)

            result.toList
        }

        // Prepend control element
        control :: holders
    }

    // Rename a control with its holders, binds, etc.
    def findRenameControl(inDoc: NodeInfo, oldName: String, newName: String) =
        if (oldName != newName) {
            findRenameHolders(inDoc, oldName, newName)
            findRenameBind(inDoc, oldName, newName)
            findControlByName(inDoc, oldName) foreach (renameControlByElement(_, newName))
            renameTemplate(inDoc, oldName, newName)
        }

    // Rename the control (but NOT its holders, binds, etc.)
    def renameControlByElement(controlElement: NodeInfo, newName: String) {

        // Set @id in any case, @ref value if present, @bind value if present
        ensureAttribute(controlElement, "id", controlId(newName))
        ensureAttribute(controlElement, "bind", bindId(newName))

        // Make the control point to its template if @origin is present
        setvalue(controlElement \@ "origin", makeInstanceExpression(templateId(newName)))

        // Set xf:label, xf:hint, xf:help and xf:alert @ref if present
        // FIXME: This code is particularly ugly!
        controlElement \ * filter (e ⇒ LHHANames(e.localname)) map
            (e ⇒ (e \@ "ref", e.localname)) filter
                (_._1 nonEmpty) filter { e ⇒
                    val ref = e._1.stringValue
                    ref.isEmpty || ref.startsWith("$form-resources")
                } foreach { e ⇒
                    setvalue(e._1, s"$$form-resources/$newName/${e._2}")
                }

        // If using a static itemset editor, set xf:itemset/@ref xf:itemset/@nodeset value
        if (hasEditor(controlElement, "static-itemset"))
            for (attName ← Seq("ref", "nodeset"))
                setvalue(controlElement \ "*:itemset" \@ attName, s"$$form-resources/$newName/item")
    }

    // Rename a bind
    def renameBindByElement(bindElement: NodeInfo, newName: String) = {
        ensureAttribute(bindElement, "id", bindId(newName))
        ensureAttribute(bindElement, "name", newName)
        ensureAttribute(bindElement, "ref", newName)
        delete(bindElement \@ "nodeset")
    }

    // Rename a bind
    def findRenameBind(inDoc: NodeInfo, oldName: String, newName: String) =
        findBindByName(inDoc, oldName) foreach
            (renameBindByElement(_, newName))

    // Rename holders with the given name
    def findRenameHolders(inDoc: NodeInfo, oldName: String, newName: String) =
        findHolders(inDoc, oldName) foreach
            (rename(_, newName))

    // Find or create a data holder for the given hierarchy of names
    private def ensureDataHolder(root: NodeInfo, holders: Seq[(() ⇒ NodeInfo, Option[String])]) = {

        @tailrec def ensure(parents: Seq[NodeInfo], names: Iterator[(() ⇒ NodeInfo, Option[String])]): Seq[NodeInfo] =
            if (names.hasNext) {
                val (getHolder, precedingHolderName) = names.next()
                val holder = getHolder() // not ideal: this might create a NodeInfo just to check the name of the holder

                val children =
                    for {
                        parent ← parents
                    } yield
                        parent \ * filter (_.name == holder.name) match {
                            case Seq() ⇒
                                // No holder exists so insert one
                                insert(into = parent, after = parent \ * filter (_.name == precedingHolderName.getOrElse("")), origin = holder)
                            case existing ⇒
                                // At least one holder exists (can be more than one for repeats)
                                existing

                        }

                ensure(children.flatten, names)
            } else
                parents

        ensure(Seq(root), holders.toIterator)
    }

    // Insert data and resource holders for all languages
    def insertHolders(controlElement: NodeInfo, dataHolder: NodeInfo, resourceHolder: NodeInfo, precedingControlName: Option[String]) {

        // Maybe add template items to the resource holder
        if (hasEditor(controlElement, "static-itemset")) {
            val fbResourceInFBLang = asNodeInfo(topLevelModel("fr-resources-model").get.getVariable("fr-form-resources"))
            val templateItems = fbResourceInFBLang \ "template" \ "items" \ "item"
            insert(into = resourceHolder, origin = templateItems)
        }
        // Create one holder per existing language
        val resourceHolders = (formResourcesRoot \ "resource" \@ "*:lang") map (_.stringValue → resourceHolder)
        insertHolders(controlElement, dataHolder, resourceHolders, precedingControlName)
    }

    // Insert data and resource holders for all languages
    def insertHolders(controlElement: NodeInfo, dataHolder: NodeInfo, resourceHolders: Seq[(String, NodeInfo)], precedingControlName: Option[String]) {
        val doc = controlElement.getDocumentRoot
        val containerNames = findContainerNames(controlElement)

        // Insert hierarchy of data holders
        // We pass a Seq of tuples, one part able to create missing data holders, the other one with optional previous names.
        // In practice, the ancestor holders should already exist.
        if (! isCustomInstance)
            ensureDataHolder(formInstanceRoot(doc), (containerNames map (n ⇒ (() ⇒ elementInfo(n), None))) :+ (() ⇒ dataHolder, precedingControlName))

        // Insert resources placeholders for all languages
        if (resourceHolders.nonEmpty) {
            val resourceHoldersMap = resourceHolders.toMap
            formResourcesRoot \ "resource" foreach (resource ⇒ {
                val lang = (resource \@ "*:lang").stringValue
                val holder = resourceHoldersMap.get(lang) getOrElse resourceHolders(0)._2
                insert(into = resource, after = resource \ * filter (_.name == precedingControlName.getOrElse("")), origin = holder)
            })
        }

        // Insert repeat template holder if needed
        if (! isCustomInstance)
            for {
                grid ← findContainingRepeat(controlElement)
                gridName ← getControlNameOption(grid)
                root ← templateRoot(doc, gridName)
            } yield
                ensureDataHolder(root, Seq((() ⇒ dataHolder, precedingControlName)))
    }

    // Update a mip for the given control, grid or section id
    // The bind is created if needed
    def updateMip(inDoc: NodeInfo, controlName: String, mipName: String, mipValue: String): Unit = {

        require(Model.AllMIPNames(mipName))
        val mipQName = convertMIP(mipName)

        findControlByName(inDoc, controlName) foreach { control ⇒

            // Get or create the bind element
            val bind = ensureBinds(inDoc, findContainerNames(control) :+ controlName)

            val valueRequiresNamespaceMapping =
                mipName == "type" && valueNamespaceMappingScopeIfNeeded(bind, mipValue).isDefined

            // NOTE: It's hard to remove the namespace mapping once it's there, as in theory lots of
            // expressions and types could use it. So for now the mapping is never garbage collected.
            val isStringType =
                valueRequiresNamespaceMapping &&
                Set(XS_STRING_QNAME, XFORMS_STRING_QNAME)(bind.resolveQName(mipValue))

            // Create/update or remove attribute
            nonEmptyOrNone(mipValue) match {
                case Some(value) if ! isStringType ⇒ ensureAttribute(bind, mipQName, value)
                case _ ⇒ delete(bind \@ mipQName)
            }
        }
    }

    // Return None if no namespace mapping is required OR none can be created
    def valueNamespaceMappingScopeIfNeeded(bind: NodeInfo, qNameValue: String): Option[(String, String)] = {

        val (prefix, _) = parseQName(qNameValue)

        def existingNSMapping =
            bind.namespaceMappings.toMap.get(prefix) map (prefix →)

        def newNSMapping = {
            // If there is no mapping and the schema prefix matches the prefix and a uri is found for the
            // schema, then insert a new mapping. We place it on the top-level bind so we don't have to insert
            // it repeatedly.
            val newURI =
                if (findSchemaPrefix(bind) == Some(prefix))
                    findSchemaNamespace(bind)
                else
                    None

            newURI map { uri ⇒
                insert(into = findTopLevelBind(bind).toList, origin = namespaceInfo(prefix, uri))
                prefix → uri
            }
        }

        if (prefix == "")
            None
        else
            existingNSMapping orElse newNSMapping
    }

    // Get the value of a MIP attribute if present
    def getMip(inDoc: NodeInfo, controlName: String, mipName: String) = {
        require(Model.AllMIPNames(mipName))
        val mipQName = convertMIP(mipName)

        findBindByName(inDoc, controlName) flatMap (bind ⇒ attValueOption(bind \@ mipQName))
    }

    private def convertMIP(mipName: String) =
        RewrittenMIPs.get(mipName) orElse (AllMIPsByName.get(mipName) map (_.aName)) getOrElse (throw new IllegalArgumentException)

    // XForms callers: find the value of a MIP or null (the empty sequence)
    def getMipOrEmpty(inDoc: NodeInfo, controlName: String, mipName: String) =
        getMip(inDoc, controlName, mipName).orNull

    // Get all control names by inspecting all elements with an id that converts to a valid name
    def getAllControlNames(inDoc: NodeInfo) =
        fbFormInstance.idsIterator filter isIdForControl map controlName

    // For XForms callers
    def getAllControlNamesXPath(inDoc: NodeInfo): SequenceIterator = getAllControlNames(inDoc) map StringValue.makeStringValue

    // Return all the controls in the view
    def getAllControlsWithIds(inDoc: NodeInfo) =
        findFRBodyElement(inDoc) \\ * filter
            (e ⇒ isIdForControl(e attValue "id"))

    // From an <xbl:binding>, return the view template (say <fr:autocomplete>)
    def viewTemplate(binding: NodeInfo) = {
        val metadata = binding \ "*:metadata"
        (((metadata \ "*:template") ++ (metadata \ "*:templates" \ "*:view")) \ *).headOption
    }

    // From an <xbl:binding>, return all bind attributes
    // They are obtained from the legacy datatype element or from templates/bind.
    def bindAttributesTemplate(binding: NodeInfo) = {
        val metadata = binding \ "*:metadata"
        val typeFromDatatype = ("", "type") → ((metadata \ "*:datatype" map (_.stringValue) headOption) getOrElse "xs:string")
        val bindAttributes = metadata \ "*:templates" \ "*:bind" \@ @* map (att ⇒ att.qname →  att.stringValue)

        typeFromDatatype +: bindAttributes filterNot
            { case ((uri, local), value) ⇒ local == "type" && value == "xs:string" } map // TODO: assume literal 'xs:' prefix (should resolve namespace)
            { case (qname, value) ⇒ attributeInfo(qname, value) }
    }

    // From a control element (say <fr:autocomplete>), returns the corresponding <xbl:binding>
    def binding(controlElement: NodeInfo) =
        asScalaSeq(topLevelModel("fr-form-model").get.getVariable("component-bindings")) map asNodeInfo find (b ⇒
            viewTemplate(b) match {
                case Some(viewTemplate) ⇒ viewTemplate.qname == controlElement.qname
                case _ ⇒ false
            })

    // Version returning a list, for called from XForms
    // TODO: Is there a better way, so we don't have to keep defining alternate functions? Maybe define a Option -> List function?
    def bindingOrEmpty(controlElement: NodeInfo) = binding(controlElement).orNull

    // Finds if a control uses a particular type of editor (say "static-itemset")
    // TODO: make `editor` something other than a string
    def hasEditor(controlElement: NodeInfo, editor: String) = {
        (binding(controlElement) filter (b ⇒ {
            val staticItemsetAttribute = (b \ "*:metadata" \ "*:editors" \@ editor).headOption
            staticItemsetAttribute match {
                case Some(a) ⇒ a.stringValue == "true"
                case _ ⇒ false
            }
        })).isDefined
    }

    // Get a node's "appearance" attribute as an Option[String]
    def appearanceOption(e: NodeInfo) =
        e \@ "appearance" map (_.stringValue) headOption

    // Given a control (e.g. xf:input) and its xf:bind, find the corresponding xbl:binding elements
    def findBindingsForControl(components: NodeInfo, control: NodeInfo, bind: NodeInfo) = {

        val stringQName = XMLConstants.XS_STRING_QNAME

        // Get QName from the bind, using xs:string if no type is found
        val controlTypeQName =
            bind \@ "type" headOption match {
                case Some(tpe) ⇒ bind.resolveQName(tpe.stringValue)
                case None ⇒ stringQName
            }

        // Support both xs:foo and xf:foo as they are considered to be equivalent when looking up the metadata. So
        // here we ignore the namespace URI to achieve this (could be more restrictive if needed).
        val controlTypeLocalname = controlTypeQName.getName
        val controlQName = control.qname
        val controlAppearanceOption = appearanceOption(control)

        // Get matching xbl:bindings
        components \ (XBL → "xbl") \ (XBL → "binding") filter { binding ⇒

            val template = viewTemplate(binding)
            val typeLocalname = bindAttributesTemplate(binding) self "type" map (_ stringValue) headOption

            // Control name and template name must match
            (template exists (_.qname == controlQName)) &&
            // Appearance must match
            (template exists (appearanceOption(_) == controlAppearanceOption)) &&
            // Type local names must match if present
            (typeLocalname.isEmpty || (typeLocalname exists (_ == controlTypeLocalname)))
        } asJava
    }

    // Find a given static control by name
    def findStaticControlByName(controlName: String) = {
        val model = getFormModel
        val part = model.getStaticModel.part
        for {
            controlId ← findControlIdByName(getFormDoc, controlName)
            prefixedId = part.startScope.prefixedIdForStaticId(controlId)
            control ← Option(part.getControlAnalysis(prefixedId))
        } yield
            control
    }

    // Find a given concrete control by name
    def findConcreteControlByName(controlName: String) = {
        val model = getFormModel
        for {
            controlId ← findControlIdByName(getFormDoc, controlName)
            control ← Option(model.container.resolveObjectByIdInScope(model.getEffectiveId, controlId, null)) map (_.asInstanceOf[XFormsControl])
        } yield
            control
    }

    // Find a control's LHHA (there can be more than one for alerts)
    def getControlLHHA(inDoc: NodeInfo, controlName: String, lhha: String) =
        findControlByName(inDoc, controlName).toList child (XF → lhha)

    // Set the control help and add/remove help element and placeholders as needed
    def setControlHelp(controlName: String,  value: String) = {

        setControlResource(controlName, "help", trimToEmpty(value))

        val inDoc = getFormDoc

        if (hasBlankOrMissingLHHAForAllLangsUseDoc(inDoc, controlName, "help"))
            removeLHHAElementAndResources(inDoc, controlName, "help")
        else
            ensureCleanLHHAElements(inDoc, controlName, "help")
    }

    // For a given control and LHHA type, whether the mediatype on the LHHA is HTML
    def isControlLHHAHTMLMediatype(inDoc: NodeInfo, controlName: String, lhha: String) =
        hasHTMLMediatype(getControlLHHA(inDoc, controlName, lhha))

    // For a given control and LHHA type, set the mediatype on the LHHA to be HTML or plain text
    def setControlLHHAMediatype(inDoc: NodeInfo, controlName: String, lhha: String, isHTML: Boolean): Unit =
        if (isHTML != isControlLHHAHTMLMediatype(inDoc, controlName, lhha))
            setHTMLMediatype(getControlLHHA(inDoc, controlName, lhha), isHTML)

    // For a given control, whether the mediatype on itemset labels is HTML
    def isItemsetHTMLMediatype(inDoc: NodeInfo, controlName: String) =
        hasHTMLMediatype(findControlByName(inDoc, controlName).toList child "itemset" child "label")

    // For a given control, set the mediatype on the itemset labels to be HTML or plain text
    def setItemsetHTMLMediatype(inDoc: NodeInfo, controlName: String, isHTML: Boolean): Unit =
        if (isHTML != isItemsetHTMLMediatype(inDoc, controlName))
            setHTMLMediatype(findControlByName(inDoc, controlName).toList child "itemset" child "label", isHTML)

    private def setHTMLMediatype(nodes: Seq[NodeInfo], isHTML: Boolean): Unit =
        nodes foreach { lhhaElement ⇒
            if (isHTML)
                insert(into = lhhaElement, origin = attributeInfo("mediatype", "text/html"))
            else
                delete(lhhaElement \@ "mediatype")
        }

    def ensureCleanLHHAElements(inDoc: NodeInfo, controlName: String, lhha: String, count: Int = 1, replace: Boolean = true): Seq[NodeInfo] = {
        val control  = findControlByName(inDoc, controlName).get
        val existing = getControlLHHA(inDoc, controlName, lhha)

        if (replace)
            delete(existing)

        // Try to insert in the right position wrt other LHHA elements. If none, will be inserted as first
        // element.

        if (count > 0) {
            val newTemplates =
                if (count == 1) {
                    def newTemplate: NodeInfo =
                        <xf:lhha xmlns:xf="http://www.w3.org/2002/xforms"
                                 ref={s"$$form-resources/$controlName/$lhha"}/>.copy(label = lhha)

                    Seq(newTemplate)
                } else {
                    def newTemplate(index: Int): NodeInfo =
                        <xf:lhha xmlns:xf="http://www.w3.org/2002/xforms"
                                 ref={s"$$form-resources/$controlName/$lhha[$index]"}/>.copy(label = lhha)

                    1 to count map newTemplate
                }

            insertElementsImposeOrder(into = control, origin = newTemplates, LHHAInOrder)
        } else
            Seq.empty
    }

    private def removeLHHAElementAndResources(inDoc: NodeInfo, controlName: String, lhha: String) = {
        val control = findControlByName(inDoc, controlName).get

        val removedHolders = delete(lhhaHoldersForAllLangsUseDoc(inDoc, controlName, lhha))
        val removedLHHA    = delete(control child (XF → lhha))

        removedHolders ++ removedLHHA
    }

    // XForms callers: build an effective id for a given static id or return null (the empty sequence)
    def buildFormBuilderControlAbsoluteIdOrEmpty(inDoc: NodeInfo, staticId: String) =
        buildFormBuilderControlEffectiveId(inDoc, staticId) map effectiveIdToAbsoluteId orNull

    def buildFormBuilderControlEffectiveIdOrEmpty(inDoc: NodeInfo, staticId: String) =
        buildFormBuilderControlEffectiveId(inDoc, staticId).orNull

    private def buildFormBuilderControlEffectiveId(inDoc: NodeInfo, staticId: String) =
        byId(inDoc, staticId) map (DynamicControlId + COMPONENT_SEPARATOR + buildControlEffectiveId(_))

    // Build an effective id for a given static id
    //
    // This assumes a certain hierarchy:
    //
    // - zero or more *:section containers
    // - zero or more fr:grid containers
    // - the only repeats are containers
    // - all containers must have stable (not automatically-generated by XForms) ids
    def buildControlEffectiveId(control: NodeInfo) = {
        val staticId = control attValue "id"

        // Ancestors from root to leaf except fb-body group if present
        val ancestorContainers = findAncestorContainers(control, includeSelf = false).reverse filterNot isFBBody

        val containerIds = ancestorContainers map (_ attValue "id")
        val repeatDepth  = ancestorContainers count IsRepeat

        def suffix = 1 to repeatDepth map (_ ⇒ 1) mkString REPEAT_INDEX_SEPARATOR_STRING
        val prefixedId = containerIds :+ staticId mkString XF_COMPONENT_SEPARATOR_STRING

        prefixedId + (if (repeatDepth == 0) "" else REPEAT_SEPARATOR + suffix)
    }

    // Find all resource holders and elements which are unneeded because the resources are blank
    def findBlankLHHAHoldersAndElements(inDoc: NodeInfo, lhha: String) = {

        val allHelpElements =
            inDoc.root \\ (XF → lhha) map
            (lhhaElement ⇒ lhhaElement → lhhaElement.attValue("ref")) collect
            { case (lhhaElement, HelpRefMatcher(controlName)) ⇒ lhhaElement → controlName }

        val allUnneededHolders =
            allHelpElements collect {
                case (lhhaElement, controlName) if hasBlankOrMissingLHHAForAllLangsUseDoc(inDoc, controlName, lhha) ⇒
                   lhhaHoldersForAllLangsUseDoc(inDoc, controlName, lhha) :+ lhhaElement
            }

        allUnneededHolders.flatten.asJava
    }
}