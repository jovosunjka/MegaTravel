package com.bsep_sbz.SIEMCenter.model.sbz.log;


import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.definition.type.Role;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@org.kie.api.definition.type.Role(Role.Type.EVENT)
@org.kie.api.definition.type.Timestamp("timestamp")
//@org.kie.api.definition.type.Expires()
public class Log {
    private Long id;
    private LogLevel type;
    private LogCategory category;
    private String source;
    private Date timestamp;
    private String hostAddress;
    private String message;
    // attribute1:value1,attribute2:value2,attribute3:value3, ...  (message format)

    private final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Log() {
        this.timestamp = new Date();
        String dataIWant = "".replaceFirst(".*'(.*?)'.*", "$1");
    }
    
    
    public Log(Long id, LogLevel type, LogCategory category, Date timestamp, String source, String hostAddress, String message) throws ParseException {
        this.id = id;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.source = source;
        this.hostAddress = hostAddress;
        this.message = message;
    }

    public Log(Long id, LogLevel type, LogCategory category, String timestampStr, String source, String hostAddress, String message) throws ParseException {
        this.id = id;
        this.type = type;
        this.category = category;
        this.timestamp = timestampStr.contains("/") ? sdf2.parse(timestampStr) : sdf1.parse(timestampStr);

        this.source = source;
        this.hostAddress = hostAddress;
        this.message = message;
    }

    public long diffrenceInHours(Date date) {
        long difference = TimeUnit.DAYS.convert(date.getTime() - timestamp.getTime(), TimeUnit.HOURS);
        System.out.println("diffrenceInHours(): " + difference);
        return difference;
    }

    public static long getDaysOfInactivity(long first, long second) {
        return TimeUnit.DAYS.convert(first - second, TimeUnit.MILLISECONDS);
    }

    public String getUsername() {
        String str = StringUtils.substringBetween(message+",","username:", ",");
        // za svaki slucaj dodajemo zarez na kraj
        //System.out.println("getUsername(): " + str);
        return str;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogLevel getType() {
        return type;
    }

    public void setType(LogLevel type) {
        this.type = type;
    }

    public LogCategory getCategory() { return category; }

    public void setCategory(LogCategory category) { this.category = category; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    /*public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    */

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return id.equals(log.id) &&
                type == log.type &&
                timestamp.equals(log.timestamp) &&
                hostAddress.equals(log.hostAddress) &&
                message.equals(log.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, timestamp, hostAddress, message);
    }

    @Override
    public String toString() {
        return "Log(id="+ id +", type="+type.name()+", category="+category.name()+", source="+source
                +", timestamp="+sdf1.format(timestamp)+", hostAddress="+hostAddress+", message=" + message+")";
    }
}
