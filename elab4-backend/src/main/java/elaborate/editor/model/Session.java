package elaborate.editor.model;

import java.util.Date;

public class Session {
	Date lastAccessed;
	long userId;
	private boolean federated = false;

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

	public boolean isFederated() {
		return federated;
	}

	public Session setFederated(boolean federated) {
		this.federated = federated;
		return this;
	}
}