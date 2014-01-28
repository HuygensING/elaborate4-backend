package elaborate.editor.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ActionTest {

  @Test
  public void test() {
    assertThat(Action.values()).isNotEmpty();
  }
}
