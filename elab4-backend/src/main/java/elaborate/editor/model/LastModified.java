package elaborate.editor.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import elaborate.editor.model.orm.User;

public class LastModified {

  private Date date;
  private User user;

  public LastModified(Date modificationDate, User modifiedBy) {
    setDate(modificationDate);
    setUser(modifiedBy);
  }

  public String getDateString() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return simpleDateFormat.format(getDate());
  }

  public String getBy() {
    return getUser().getUsername();
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
