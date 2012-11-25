package charkes.siarhei.mindstorms.exception;

public class MindstormsException extends Exception {
	public MindstormsException() {
		super();
	}
	public MindstormsException(String detailMessage) {
		super(detailMessage);
	}
	public MindstormsException(Throwable throwable) {
		super(throwable);
	}
}
