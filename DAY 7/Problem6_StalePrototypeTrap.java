
package com.ioc.problem1;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Scope("prototype")
class ReportTask {

    private String data;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void execute() {
        System.out.println("[ReportTask@" + System.identityHashCode(this) + "] Processing: " + data);
    }
}


@Service
class ReportManager {

    private final ObjectFactory<ReportTask> taskFactory;

    // Constructor Injection of ObjectFactory
    @Autowired
    public ReportManager(ObjectFactory<ReportTask> taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void generateReport(String data) {
        // Each invocation gets a NEW, isolated ReportTask instance
        ReportTask task = taskFactory.getObject();
        task.setData(data);
        task.execute();
    }
}

class Problem6Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 6: The Stale Prototype Trap ===");
        System.out.println();
        System.out.println("DEFECT: Singleton ReportManager injects Prototype ReportTask once.");
        System.out.println("  -> @Scope(\"prototype\") is rendered useless.");
        System.out.println("  -> All threads share the same ReportTask instance.");
        System.out.println();
        System.out.println("FIX: Inject ObjectFactory<ReportTask> instead.");
        System.out.println("  -> taskFactory.getObject() reaches into the IoC container at runtime.");
        System.out.println("  -> Each call creates a fresh, isolated prototype instance.");
        System.out.println("  -> Thread safety guaranteed without 'new' keyword or ApplicationContext coupling.");
    }
}
