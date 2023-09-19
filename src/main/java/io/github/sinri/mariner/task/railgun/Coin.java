package io.github.sinri.mariner.task.railgun;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class Coin<T> {
    private final String coinId;
    private @Nullable Future<T> future = null;
    private boolean done = false;
    private T result = null;
    private boolean failed = false;
    private RuntimeException failure = null;
    private Coin<?> nextCoin;
    private CoinHandler<T, ?> coinHandler;

    public Coin() {
        this.coinId = UUID.randomUUID().toString();
    }

    public Coin(@NotNull Future<T> future) {
        this.coinId = UUID.randomUUID().toString();
        this.future = future;
    }

    public static <R> Coin<R> withResult(R r) {
        return Railgun.fire(() -> r);
    }

    public String coinId() {
        return this.coinId;
    }

    public boolean isDone() {
        return this.done;
    }

    public T getResult() {
        return this.result;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public RuntimeException getFailure() {
        return failure;
    }

    public void refreshStatus() {
        if (this.future == null) {
            return;
        }
        if (this.done || this.failed) {
            return;
        }
        if (this.future.isDone()) {
            try {
                try {
                    this.result = this.future.get();
                    this.done = true;
                } catch (Throwable e) {
                    throw new RuntimeException(e.getCause() == null ? e : e.getCause());
                }
            } catch (RuntimeException e) {
                this.failure = e;
                this.failed = true;
            }
        }
    }

    public <R> Coin<R> compose(@NotNull Function<T, R> doneF) {
        return compose(doneF, null);
    }

    public <R> Coin<R> compose(@NotNull Function<T, R> doneF, @Nullable Function<Throwable, R> failedF) {
        nextCoin = new Coin<R>();
        this.coinHandler = new CoinHandler<T, R>(
                this,
                (Coin<R>) nextCoin,
                doneF,
                failedF
        );
        return (Coin<R>) nextCoin;
    }

    public void compose(@Nullable Consumer<T> doneC, @Nullable Consumer<Throwable> failedC) {
        this.compose(r -> {
            if (doneC != null)
                doneC.accept(r);
            return null;
        }, e -> {
            if (failedC != null)
                failedC.accept(e);
            return null;
        });
    }

    boolean hasCoinHandler() {
        return this.coinHandler != null;
    }

    @Nullable Object handleForNextByWorker() {
        if (this.coinHandler != null) {
            return this.coinHandler.handleByWorker();
        }
        return null;
    }

    void absorb(Coin<?> anotherCoin) {
        this.future = (Future<T>) anotherCoin.future;
    }

    public Coin<?> getNextCoin() {
        return nextCoin;
    }
}
