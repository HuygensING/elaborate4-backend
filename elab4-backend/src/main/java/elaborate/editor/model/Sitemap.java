package elaborate.editor.model;

import java.util.Comparator;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import elaborate.jaxrs.JAXUtils;
import elaborate.jaxrs.JAXUtils.API;

@XmlRootElement
public class Sitemap {
  private static final Comparator<API> PATH_COMPARATOR = new Comparator<JAXUtils.API>() {
    @Override
    public int compare(API a1, API a2) {
      return a1.path.compareTo(a2.path);
    }
  };
  private static final Comparator<API> REQUESTTYPES_COMPARATOR = new Comparator<JAXUtils.API>() {
    @Override
    public int compare(API a1, API a2) {
      return a1.requestTypes.toString().compareTo(a2.requestTypes.toString());
    }
  };
  public final String description = "Elaborate backend sitemap";
  public final ImmutableList<API> availableAPIList;

  public Sitemap(Application application) {
    List<API> list = Lists.newArrayList();
    for (Class<?> cls : application.getClasses()) {
      List<API> apis = JAXUtils.generateAPIs(cls);
      list.addAll(apis);
    }
    availableAPIList = ImmutableList.copyOf(Ordering.from(PATH_COMPARATOR).compound(REQUESTTYPES_COMPARATOR).sortedCopy(list));
  }

}
