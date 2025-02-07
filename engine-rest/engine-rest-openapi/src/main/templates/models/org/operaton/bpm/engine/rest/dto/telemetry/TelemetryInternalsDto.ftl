<#macro dto_macro docsUrl="">
<@lib.dto>

    <@lib.property
        name = "database"
        type = "ref"
        additionalProperties = false
        dto = "AbstractVendorVersionInformationDto"
        desc = "Vendor and version of the connected database."/>

    <@lib.property
        name = "application-server"
        type = "ref"
        additionalProperties = false
        dto = "AbstractVendorVersionInformationDto"
        desc = "Vendor and version of the application server."/>

    <@lib.property
        name = "operaton-integration"
        type = "array"
        itemType = "string"
        desc = "List of Operaton integrations used (e.g., Operaton Spring Boot Starter, Operaton Run, WildFly/JBoss subsystem, Operaton EJB)."/>

    <@lib.property
        name = "commands"
        type = "object"
        additionalProperties = true
        dto = "TelemetryCountDto"
        desc = "The count of executed commands after the last retrieved data."/>

    <@lib.property
        name = "metrics"
        type = "object"
        additionalProperties = true
        dto = "TelemetryCountDto"
        desc = "The collected metrics are the number of root process instance executions started, the number of activity instances started or also known as flow node instances, and the number of executed decision instances and elements."/>

    <@lib.property
        name = "webapps"
        type = "array"
        itemType = "string"
        desc = "The webapps enabled in this installation of Operaton."/>

    <@lib.property
        name = "jdk"
        type = "ref"
        additionalProperties = false
        dto = "AbstractVendorVersionInformationDto"
        desc = "Vendor and version of the installed JDK."/>

    <@lib.property
        name = "data-collection-start-date"
        type = "string"
        format = "date-time"
        nullable = false
        last = true
        desc = "The date when the engine started to collect dynamic data, such as command executions and metrics. If telemetry sending is enabled, dynamic data resets on sending the data to Operaton.
                Dynamic data and the date returned by this method are reset in three cases: engine startup, after engine start when sending telemetry data to Operaton is enabled via API, after sending telemetry data to Operaton (only when this was enabled)
                The date is in the format <code>YYYY-MM-DD'T'HH:mm:ss.SSSZ</code>."/>

</@lib.dto>

</#macro>