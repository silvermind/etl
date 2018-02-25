package io.adopteunops.etl.service.parser;

import io.adopteunops.etl.domain.CEFDataType;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static io.adopteunops.etl.domain.CEFDataType.*;

@AllArgsConstructor
public enum CEFDictionary {
    act("deviceAction", string, "Action mentioned in the event"),
    app("applicationProtocol", string, "Application level protocol. example values are: HTTP,HTTPS, SSHv2, Telnet, POP,IMAP, IMAPS, etc."),
    in("bytesIn", integer, "Number of bytes transferred inbound. Inbound relative to the source to destination relationship, meaning that data was flowing from source to destination."),
    out("bytesOut", integer, "Number of bytes transferred outbound. Outbound relative to the source to destination relationship, meaning that data was flowing from destination to source."),
    dst("destinationAddress", ipv4, "Address Identifies destination that the event refers to in an IP network. The format is an IPv4 address. Example: 192.168.10.1"),
    dhost("destinationHostName", fqdn, "Identifies the destination that the event refers to in an IP network. The format is a fully qualified domain name associated with the destination node. Example: zurich.domain.com"),
    dmac("destinationMacAddress", string, "Six colon-separated hexadecimal numbers. Example: 00:0D:60:AF:1B:61"),
    dntdom("destinationNtDomain", string, " The Windows domain name of the destination address."),
    dpt("destinationPort", integer, "The valid port numbers are between 0 and 65535."),
    dproc("destinationProcessName", string, "The name of the process which is the event's destination. For example: telnetd, or sshd."),
    duid("destinationUserId", string, "Identifies the destination user by ID. For example, in UNIX, the root user is generally associated with user ID 0."),
    dpriv("destinationUserPrivileges", string, "The allowed values are: “Administrator”, “User”, and “Guest”. This identifies the destination user's privileges. In UNIX, for example, activity executed on the root user would be identified with destinationUserPrivileges of “Administrator”. This is an idealized and simplified view on privileges and can be extended in the future."),
    duser("destinationUserName", string, "Identifies the destination user by name. This is the user associated with the event's destination. E-mail addresses are also mapped into the UserName fields. The recipient is a candidate to put into destinationUserName."),
    end("endTime", timestamp, "The time at which the activity related to the event ended. The format is MMM dd yyyy HH:mm:ss or milliseconds since epoch (Jan 1st 1970). An example would be reporting the end of a session."),
    fname("fileName", string, "Name of the file"),
    fsize("fileSize", integer, "Size of the file"),
    msg("message", string, "An arbitrary message giving more details about the event. Multi-line entries can be produced by using \\n as the new-line separator."),
    rt("receiptTime", timestamp, "The time at which the event related to the activity was received. The format is MMM dd yyyy HH:mm:ss or milliseconds since epoch (Jan 1st 1970)."),
    request("requestURL", string, "In the case of an HTTP request, this field contains the URL accessed. The URL should contain the protocol as well, e.g., “http://www.security.com”"),
    src("sourceAddress", mac_address, "Identifies the source that an event refers to in an IP network. The format is an IPv4 address. Example: “192.168.10.1”"),
    shost("sourceHostName", fqdn, "Identifies the source that an event refers to in an IP network. The format is a fully qualified domain name associated with the source node. Example: “zurich.domain.com”"),
    smac("sourceMacAddress", mac_address, "Six colon-separated hexadecimal numbers. Example: “00:0D:60:AF:1B:61”"),
    sntdom("sourceNtDomain", string, "The Windows domain name for the source address"),
    spt("sourcePort", integer, "The valid port numbers are 0 to 65535."),
    spriv("sourceUser", string, "The allowed values are: “Administrator”, “User”, and “Guest”. It identifies the source user's privileges. In UNIX, for example, activity executed by the root user would be identified with sourceUserPrivileges of “Administrator”. This is an idealized and simplified view on privileges and can be extended in the future."),
    suid("sourceUserId", string, "Identifies the source user by ID. This is the user associated with the source of the event. For example, in UNIX, the root user is generally associated with user ID 0."),
    suser("sourceUserName", string, "Identifies the source user by name. E-mail addresses are also mapped into the UserName fields. The sender is a candidate to put into sourceUserName."),
    start("startTime", timestamp, "The time when the activity the event referred to started. The format is MMM dd yyyy HH:mm:ss or milliseconds since epoch (Jan 1st 1970)."),
    proto("transportProtocol", string, "Identifies the Layer-4 protocol used. The possible values are protocol names such as TCP or UDP.");
    public static Map<String, String> CODE_TO_FULLNAME = buildCodeToFullName(CEFDictionary.values());
    private final String fullName;
    private final CEFDataType dataType;
    private final String description;

    private static Map<String, String> buildCodeToFullName(CEFDictionary[] values) {
        Map<String, String> ret = new HashMap<>();
        for (CEFDictionary value : values) {
            ret.put(value.name(), value.fullName);
        }
        return ret;
    }

    public static String toFullName(String code) {
        return CODE_TO_FULLNAME.getOrDefault(code, code);
    }

}
