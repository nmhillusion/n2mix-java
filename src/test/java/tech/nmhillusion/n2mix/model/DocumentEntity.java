package tech.nmhillusion.n2mix.model;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.Date;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-09-23
 */
public class DocumentEntity extends Stringeable {
    private String id;
    private String title;
    private String insertBy;
    private Date insertDataTime;

    public String getId() {
        return id;
    }

    public DocumentEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public DocumentEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getInsertBy() {
        return insertBy;
    }

    public DocumentEntity setInsertBy(String insertBy) {
        this.insertBy = insertBy;
        return this;
    }

    public Date getInsertDataTime() {
        return insertDataTime;
    }

    public DocumentEntity setInsertDataTime(Date insertDataTime) {
        this.insertDataTime = insertDataTime;
        return this;
    }
}
