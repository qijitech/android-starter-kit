package starter.kit.model.entity;

public abstract class Entity {

  /**
   * 实体主键
   */
  public abstract String identifier();

  public abstract String paginatorKey();
}
