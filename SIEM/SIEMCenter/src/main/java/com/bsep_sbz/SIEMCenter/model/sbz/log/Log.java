package com.bsep_sbz.SIEMCenter.model.sbz.log;

import com.bsep_sbz.SIEMCenter.helper.HelperMethods;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import org.kie.api.definition.type.Role;
import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Entity
@org.kie.api.definition.type.Role(Role.Type.EVENT)
@org.kie.api.definition.type.Timestamp("timestamp")
@org.kie.api.definition.type.Expires("3m")
public class Log {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private LogLevel type;

    @Column(name = "category", nullable = false)
    private LogCategory category;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Column(name = "hostAddress", nullable = false)
    private String hostAddress;

    @Column(name = "message", nullable = false)
    private String message;
    // attribute1:value1,attribute2:value2,attribute3:value3, ...  (message format)

    @Column(name = "username")
    private String username;

    @Transient
    private final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");

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
        this.timestamp = new Date(timestampStr);
        this.source = source;
        this.hostAddress = hostAddress;
        this.message = message;
    }
    // koristi se za pravilo 90+ days of inactivity
    public long diffrenceInHours(Date date) {
        return TimeUnit.DAYS.convert(date.getTime() - timestamp.getTime(), TimeUnit.HOURS);
    }

    public static long getDaysOfInactivity(long first, long second) {
        return TimeUnit.DAYS.convert(first - second, TimeUnit.MILLISECONDS);
    }

    public String getUsername() {
        String username = HelperMethods.getUsername(message);
        if(!username.equals("")) {
            return username;
        }

        return null;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return this.hashCode() == o.hashCode();
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
