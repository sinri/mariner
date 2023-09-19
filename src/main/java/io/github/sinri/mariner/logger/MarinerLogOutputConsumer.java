package io.github.sinri.mariner.logger;

import io.github.sinri.mariner.helper.MarinerHelper;

import java.util.Date;

public class MarinerLogOutputConsumer implements MarinerLogConsumer {
    private static final MarinerLogOutputConsumer instance = new MarinerLogOutputConsumer();
    private final MarinerHelper marinerHelper;

    private MarinerLogOutputConsumer() {
        this.marinerHelper = new MarinerHelper();
    }

    public static MarinerLogOutputConsumer getInstance() {
        return instance;
    }

    @Override
    public void accept(MarinerLog marinerLog) {
        StringBuilder sb = new StringBuilder();

        String dateExpression = marinerHelper.getDateExpression(new Date(marinerLog.getTimestamp()), "yyyy-MM-dd HH:mm:ss");

        sb.append(dateExpression)
                .append(" [").append(marinerLog.getLevel()).append("]")
                .append(" <").append(marinerLog.getTopic()).append(">")
                .append(" ").append(marinerHelper.jsonify(marinerLog.getAttributes()));

        System.out.println(sb);
    }
}
