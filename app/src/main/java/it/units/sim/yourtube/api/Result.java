package it.units.sim.yourtube.api;

public abstract class Result<T> {

    private Result() {}

    public static final class Success<T> extends Result<T> {

        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

    }

    public static final class Error<T> extends Result<T> {

        private final Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }

}
