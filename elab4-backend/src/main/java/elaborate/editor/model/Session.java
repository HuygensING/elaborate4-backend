package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Date;

public class Session {
	Date lastAccessed;
	final long userId;
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

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public Session setFederated(boolean federated) {
		this.federated = federated;
		return this;
	}
}
