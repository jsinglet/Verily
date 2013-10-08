[#ftl]
<#macro layout name>

    [#list templates as theTemplate]

        <#if name = "${theTemplate.name}">
            ${theTemplate.content}
        </#if>

    [/#list]

</#macro>