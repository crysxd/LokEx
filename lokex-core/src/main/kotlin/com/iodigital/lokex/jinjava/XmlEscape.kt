package com.iodigital.lokex.jinjava

import com.hubspot.jinjava.interpret.JinjavaInterpreter
import com.hubspot.jinjava.lib.filter.Filter
import org.apache.commons.lang3.StringEscapeUtils

internal class XmlEscape : Filter {
    override fun getName() = "escape_xml"

    override fun filter(`var`: Any?, interpreter: JinjavaInterpreter?, vararg args: String?): Any {
        return StringEscapeUtils.escapeXml11(`var` as String)
    }
}