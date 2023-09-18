package io.github.sinri.mariner.task.timeline;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class Fruit {
    private final String fruitId;
    private Object seed;
    private boolean failed;
    private boolean done;
    private Object result;
    private Throwable failure;
    private Function<Object, Object> plantFunc;
    private Consumer<Object> successConsumer;
    private Consumer<Throwable> failureConsumer;

    private Fruit(Object seed) {
        this.fruitId = UUID.randomUUID().toString();

        this.seed = seed;
        this.done = false;
        this.failed = false;
        this.result = null;

        this.plantFunc = null;
    }

    public static Fruit sinceSuccess(Object seed) {
        Fruit f = new Fruit(seed);
        //f.plant();
        f.result = seed;
        f.done = true;
        return f;
    }

    public static Fruit sinceFailure(Throwable throwable) {
        Fruit f = new Fruit(null);
        //f.plant();
        f.failure = throwable;
        f.failed = true;
        return f;
    }

    protected void plant() {
        System.out.println("Fruit[" + fruitId + "].plant.initialize");
        Timeline.register(TimelinePlan.oneShot(() -> {
            System.out.println("Fruit[" + fruitId + "].plant.start");
            try {
                if (plantFunc == null) {
                    this.result = seed;
                } else {
                    this.result = plantFunc.apply(seed);
                }
                this.done = true;

                if (this.successConsumer != null) {
                    this.successConsumer.accept(this.result);
                } else {
                    System.out.println("timeline execute without successConsumer");
                }
            } catch (Exception e) {
                this.failure = e;
                this.failed = true;

                if (this.failureConsumer != null) {
                    this.failureConsumer.accept(this.failure);
                } else {
                    System.out.println("timeline execute without failureConsumer: " + this.failure);
                    this.failure.printStackTrace();
                }
            }
            System.out.println("Fruit[" + fruitId + "].plant.end");
        }));
    }

    public void onSuccess(Consumer<Object> successConsumer) {
        this.onCompletion(successConsumer, null);

//        if (this.done) {
//            successConsumer.accept(this.result);
//        } else {
//            this.successConsumer = successConsumer;
//        }
    }

    public void onFailure(Consumer<Throwable> failureConsumer) {
        this.onCompletion(null, failureConsumer);
//        if (this.failed) {
//            failureConsumer.accept(this.failure);
//        } else {
//            this.failureConsumer = failureConsumer;
//        }
    }

    public void onCompletion(Consumer<Object> successConsumer, Consumer<Throwable> failureConsumer) {
        if (this.done || this.failed) {
            if (this.done) {
                if (successConsumer != null) {
                    successConsumer.accept(this.result);
                }
            } else {
                if (failureConsumer != null) {
                    failureConsumer.accept(this.failure);
                }
            }
        } else {
            this.successConsumer = successConsumer;
            this.failureConsumer = failureConsumer;

            System.out.println("Fruit[" + fruitId + "].onCompletion.mounted");
        }
    }

    public Fruit afterSuccess(Function<Object, Object> afterSuccess) {
        return after(afterSuccess, null);
    }

    public Fruit afterFailure(Function<Throwable, Object> afterFailure) {
        return after(null, afterFailure);
    }

    public Fruit after(Function<Object, Object> afterSuccess, Function<Throwable, Object> afterFailure) {
        Fruit f = new Fruit(null);

        Consumer<Object> successConsumer = null;
        Consumer<Throwable> failureConsumer = null;

        if (afterSuccess != null) {
            successConsumer = o -> {
                f.plantFunc = afterSuccess;
                if (this.done) {
                    f.plant();
                }
            };
        }
        if (afterFailure != null) {
            failureConsumer = e -> {
                f.seed = e;
                f.plantFunc = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        return afterFailure.apply((Throwable) o);
                    }
                };

                if (this.failed) {
                    f.plant();
                }
            };
        }

        this.onCompletion(successConsumer, failureConsumer);
        return f;
    }

}
