package elaborate.editor.publish;

import java.util.Map;

import com.google.common.collect.Maps;

public class Publisher {
  private static Publisher instance;
  Map<String, PublishTask> taskIndex = Maps.newHashMap();

  private Publisher() {}

  public static Publisher instance() {
    if (instance == null) {
      instance = new Publisher();
    }
    return instance;
  }

  public Publication.Status publish(Publication.Settings settings) {
    PublishTask publishTask = new PublishTask(settings);
    taskIndex.put(publishTask.getStatus().getId(), publishTask);
    new Thread(publishTask).start();
    return publishTask.getStatus();
  }

  public Publication.Status getStatus(String id) {
    PublishTask publishTask = taskIndex.get(id);
    return (publishTask != null) ? publishTask.getStatus() : null;
  }

}
