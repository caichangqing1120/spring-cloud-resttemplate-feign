package com.cht.rst.feign.inner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Logger {

    protected static String methodTag(String configKey) {
        return new StringBuilder().append('[').append(configKey.substring(0, configKey.indexOf('(')))
                .append("] ").toString();
    }

    /**
     * Override to log requests and responses using your own implementation. Messages will be http
     * request and response text.
     *
     * @param configKey value of {@link ChtFeign#configKey(Class, java.lang.reflect.Method)}
     * @param format {@link java.util.Formatter format string}
     * @param args arguments applied to {@code format}
     */
    protected abstract void log(String configKey, String format, Object... args);

//    protected void logRequest(String configKey, feign.Logger.Level logLevel, Request request) {
//        log(configKey, "---> %s %s HTTP/1.1", request.httpMethod().name(), request.url());
//        if (logLevel.ordinal() >= feign.Logger.Level.HEADERS.ordinal()) {
//
//            for (String field : request.headers().keySet()) {
//                for (String value : valuesOrEmpty(request.headers(), field)) {
//                    log(configKey, "%s: %s", field, value);
//                }
//            }
//
//            int bodyLength = 0;
//            if (request.body() != null) {
//                bodyLength = request.body().length;
//                if (logLevel.ordinal() >= feign.Logger.Level.FULL.ordinal()) {
//                    String bodyText =
//                            request.charset() != null ? new String(request.body(), request.charset()) : null;
//                    log(configKey, ""); // CRLF
//                    log(configKey, "%s", bodyText != null ? bodyText : "Binary data");
//                }
//            }
//            log(configKey, "---> END HTTP (%s-byte body)", bodyLength);
//        }
//    }

    protected void logRetry(String configKey, Logger.Level logLevel) {
        log(configKey, "---> RETRYING");
    }

//    protected Response logAndRebufferResponse(String configKey,
//                                              feign.Logger.Level logLevel,
//                                              Response response,
//                                              long elapsedTime)
//            throws IOException {
//        String reason =
//                response.reason() != null && logLevel.compareTo(feign.Logger.Level.NONE) > 0 ? " " + response.reason()
//                        : "";
//        int status = response.status();
//        log(configKey, "<--- HTTP/1.1 %s%s (%sms)", status, reason, elapsedTime);
//        if (logLevel.ordinal() >= feign.Logger.Level.HEADERS.ordinal()) {
//
//            for (String field : response.headers().keySet()) {
//                for (String value : valuesOrEmpty(response.headers(), field)) {
//                    log(configKey, "%s: %s", field, value);
//                }
//            }
//
//            int bodyLength = 0;
//            if (response.body() != null && !(status == 204 || status == 205)) {
//                // HTTP 204 No Content "...response MUST NOT include a message-body"
//                // HTTP 205 Reset Content "...response MUST NOT include an entity"
//                if (logLevel.ordinal() >= feign.Logger.Level.FULL.ordinal()) {
//                    log(configKey, ""); // CRLF
//                }
//                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
//                bodyLength = bodyData.length;
//                if (logLevel.ordinal() >= feign.Logger.Level.FULL.ordinal() && bodyLength > 0) {
//                    log(configKey, "%s", decodeOrDefault(bodyData, UTF_8, "Binary data"));
//                }
//                log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
//                return response.toBuilder().body(bodyData).build();
//            } else {
//                log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
//            }
//        }
//        return response;
//    }

    protected IOException logIOException(String configKey,
                                         Logger.Level logLevel,
                                         IOException ioe,
                                         long elapsedTime) {
        log(configKey, "<--- ERROR %s: %s (%sms)", ioe.getClass().getSimpleName(), ioe.getMessage(),
                elapsedTime);
        if (logLevel.ordinal() >= Logger.Level.FULL.ordinal()) {
            StringWriter sw = new StringWriter();
            ioe.printStackTrace(new PrintWriter(sw));
            log(configKey, "%s", sw.toString());
            log(configKey, "<--- END ERROR");
        }
        return ioe;
    }

    /**
     * Controls the level of logging.
     */
    public enum Level {
        /**
         * No logging.
         */
        NONE,
        /**
         * Log only the request method and URL and the response status code and execution time.
         */
        BASIC,
        /**
         * Log the basic information along with request and response headers.
         */
        HEADERS,
        /**
         * Log the headers, body, and metadata for both requests and responses.
         */
        FULL
    }

    /**
     * Logs to System.err.
     */
    public static class ErrorLogger extends Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            System.err.printf(methodTag(configKey) + format + "%n", args);
        }
    }

    /**
     * Logs to the category {@link feign.Logger} at {@link java.util.logging.Level#FINE}, if loggable.
     */
//    public static class JavaLogger extends feign.Logger {
//
//        final java.util.logging.Logger logger =
//                java.util.logging.Logger.getLogger(feign.Logger.class.getName());
//
//        @Override
//        protected void logRequest(String configKey, Level logLevel, Request request) {
//            if (logger.isLoggable(java.util.logging.Level.FINE)) {
//                super.logRequest(configKey, logLevel, request);
//            }
//        }
//
//        @Override
//        protected Response logAndRebufferResponse(String configKey,
//                                                  Level logLevel,
//                                                  Response response,
//                                                  long elapsedTime)
//                throws IOException {
//            if (logger.isLoggable(java.util.logging.Level.FINE)) {
//                return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
//            }
//            return response;
//        }
//
//        @Override
//        protected void log(String configKey, String format, Object... args) {
//            if (logger.isLoggable(java.util.logging.Level.FINE)) {
//                logger.fine(String.format(methodTag(configKey) + format, args));
//            }
//        }
//
//        /**
//         * Helper that configures java.util.logging to sanely log messages at FINE level without
//         * additional formatting.
//         */
//        public JavaLogger appendToFile(String logfile) {
//            logger.setLevel(java.util.logging.Level.FINE);
//            try {
//                FileHandler handler = new FileHandler(logfile, true);
//                handler.setFormatter(new SimpleFormatter() {
//                    @Override
//                    public String format(LogRecord record) {
//                        return String.format("%s%n", record.getMessage()); // NOPMD
//                    }
//                });
//                logger.addHandler(handler);
//            } catch (IOException e) {
//                throw new IllegalStateException("Could not add file handler.", e);
//            }
//            return this;
//        }
//    }

//    public static class NoOpLogger extends feign.Logger {
//
//        @Override
//        protected void logRequest(String configKey, Level logLevel, Request request) {}
//
//        @Override
//        protected Response logAndRebufferResponse(String configKey,
//                                                  Level logLevel,
//                                                  Response response,
//                                                  long elapsedTime)
//                throws IOException {
//            return response;
//        }
//
//        @Override
//        protected void log(String configKey, String format, Object... args) {}
//    }
}
