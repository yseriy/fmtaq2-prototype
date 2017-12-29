package ys.prototype.fmtaq2.domain.command;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance
abstract class Task {

    @Id
    private UUID id;
    private LocalDateTime startTimestamp;
    private LocalDateTime statusTimestamp;
    private String account;
    private String serviceType;
    private TaskStatus taskStatus;

    @Version
    private Long version;

    protected Task() {
    }

    Task(final UUID id, final String account, final String serviceType) {
        LocalDateTime createTime = LocalDateTime.now();
        this.id = id;
        this.startTimestamp = createTime;
        this.statusTimestamp = createTime;
        this.account = account;
        this.serviceType = serviceType;
        this.taskStatus = TaskStatus.NEW;
    }

    TaskStatus getStatus() {
        return taskStatus;
    }

    void setRunStatus() {
        statusTimestamp = LocalDateTime.now();
        taskStatus = TaskStatus.RUN;
    }

    void setOkStatus() {
        statusTimestamp = LocalDateTime.now();
        taskStatus = TaskStatus.OK;
    }

    void setErrorStatus() {
        statusTimestamp = LocalDateTime.now();
        taskStatus = TaskStatus.ERROR;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
