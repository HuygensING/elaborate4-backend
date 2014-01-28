package elaborate.editor.export.tei;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.AnnotationType;

public class TeiConversionConfig {
  private String groupTextsByMetadata;
  private final Map<AnnotationType, Function<Annotation, TagInfo>> annotationTypeMapper = Maps.newHashMap();

  public TeiConversionConfig setGroupTextsByMetadata(String _groupTextsByMetadata) {
    this.groupTextsByMetadata = _groupTextsByMetadata;
    return this;
  }

  public TeiConversionConfig addAnnotationTypeMapping(AnnotationType type, Function<Annotation, TagInfo> mapping) {
    annotationTypeMapper.put(type, mapping);
    return this;
  }

  public String getGroupTextsByMetadata() {
    return groupTextsByMetadata;
  }

  public Map<AnnotationType, Function<Annotation, TagInfo>> getAnnotationTypeMapper() {
    return annotationTypeMapper;
  }

}
