package tech.nmhillusion.n2mix.model;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-10-06
 */
public class StaffEntity extends Stringeable {
    private long id;
    private String name;
    private String department;
    private StaffEntity manager;

    private List<StaffEntity> colleagues;

    public long getId() {
        return id;
    }

    public StaffEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public StaffEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getDepartment() {
        return department;
    }

    public StaffEntity setDepartment(String department) {
        this.department = department;
        return this;
    }

    public StaffEntity getManager() {
        return manager;
    }

    public StaffEntity setManager(StaffEntity manager) {
        this.manager = manager;
        return this;
    }

    public List<StaffEntity> getColleagues() {
        return colleagues;
    }

    public StaffEntity setColleagues(List<StaffEntity> colleagues) {
        this.colleagues = colleagues;
        return this;
    }
}
