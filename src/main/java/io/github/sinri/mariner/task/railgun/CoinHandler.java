package io.github.sinri.mariner.task.railgun;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CoinHandler<I, O> {
    private final Coin<I> inputCoin;
    private final Coin<O> outputCoin;
    private final @Nullable Function<I, O> doneF;
    private final @Nullable Function<Throwable, O> failedF;

    public CoinHandler(
            Coin<I> inputCoin,
            Coin<O> outputCoin,
            @Nullable Function<I, O> doneF,
            @Nullable Function<Throwable, O> failedF
    ) {
        this.inputCoin = inputCoin;
        this.outputCoin = outputCoin;
        this.doneF = doneF;
        this.failedF = failedF;
    }

    public Coin<O> getOutputCoin() {
        return outputCoin;
    }

    public O handleByWorker() {
        if (inputCoin.isDone()) {
            if (doneF != null) {
                return doneF.apply(inputCoin.getResult());
            } else {
                return null;
            }
        } else if (inputCoin.isFailed()) {
            if (failedF != null) {
                return failedF.apply(inputCoin.getFailure());
            } else {
                throw inputCoin.getFailure();
            }
        }
        throw new IllegalStateException();
    }
}
