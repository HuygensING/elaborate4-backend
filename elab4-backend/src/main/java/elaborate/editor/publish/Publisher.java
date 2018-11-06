package elaborate.editor.publish;

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

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class Publisher {
	private static Publisher instance;
	private final Map<String, PublishTask> taskIndex = Maps.newHashMap();

	private Publisher() {}

	public static Publisher instance() {
		if (instance == null) {
			instance = new Publisher();
		}
		return instance;
	}

	public Publication.Status publish(Publication.Settings settings) {
		PublishTask publishTask = newOrRunningTask(settings);
		return publishTask.getStatus();
	}

	private PublishTask newOrRunningTask(Publication.Settings settings) {
		PublishTask publishTask = findRunningTaskForProject(settings.getProjectId());
		if (publishTask == null) {
			publishTask = new PublishTask(settings);
			taskIndex.put(publishTask.getStatus().getId(), publishTask);
			new Thread(publishTask).start();
		}
		return publishTask;
	}

	private PublishTask findRunningTaskForProject(Long projectId) {
		PublishTask runningTask = null;
		List<PublishTask> publishTasks = ImmutableList.copyOf(taskIndex.values());
		for (PublishTask publishTask : publishTasks) {
			if (publishTask.getProjectId() == projectId) {
				if (publishTask.getStatus().isDone()) {
					// remove PublishingTasks that are done for the given projectId
					taskIndex.remove(publishTask.getStatus().getId());
				} else {
					runningTask = publishTask;
				}
			}
		}
		return runningTask;
	}

	public Publication.Status getStatus(String id) {
		PublishTask publishTask = taskIndex.get(id);
		return (publishTask != null) ? publishTask.getStatus() : null;
	}

}
