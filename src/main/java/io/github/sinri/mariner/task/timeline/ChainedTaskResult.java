package io.github.sinri.mariner.task.timeline;

public class ChainedTaskResult {
    private Object result = null;
    private Throwable failure = null;
    private boolean done = false;
    private boolean failed = false;


    private ChainedTaskResult() {
        this.result = null;
        this.failure = null;
        this.done = false;
        this.failed = false;
    }

    public static ChainedTaskResult doneTask(Object result) {
        ChainedTaskResult task = new ChainedTaskResult();
        task.result = result;
        task.done = true;
        return task;
    }

    public static ChainedTaskResult failedTask(Throwable failure) {
        ChainedTaskResult task = new ChainedTaskResult();
        task.failure = failure;
        task.failed = true;
        return task;
    }

//    private ChainedTaskResultTransformer<T,?> taskResultTransformer;
//    private ChainedTaskResult<?> nextTaskResult;
//
//    public <R> ChainedTaskResult<R> transform(ChainedTaskResultTransformer<T,R> taskResultTransformer){
//        nextTaskResult = new ChainedTaskResult<>();
//        this.taskResultTransformer=taskResultTransformer.initializeRelationship(this, (ChainedTaskResult<R>) nextTaskResult);
//    }

    public Object getResult() {
        return result;
    }

    public Throwable getFailure() {
        return failure;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isFailed() {
        return failed;
    }
}
