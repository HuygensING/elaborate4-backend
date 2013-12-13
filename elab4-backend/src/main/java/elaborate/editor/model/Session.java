package elaborate.editor.model;

import java.util.Date;

public class Session {
	Date lastAccessed;
	long userId;

	public Session(long userId) {
		this.userId = userId;
		lastAccessed = new Date();
	}

	public long getUserId() {
		return userId;
	}

	public void update() {
		lastAccessed = new Date();
	}

	public boolean isActive() {
		long diff = new Date().getTime() - lastAccessed.getTime();
		return (diff < SessionService.SESSION_TIMEOUT);
	}
}