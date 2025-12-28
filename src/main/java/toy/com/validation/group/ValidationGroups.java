package toy.com.validation.group;
/**
  * Validation groups for request-specific constraints.
  * Keep VO reusable for search/list without triggering required validations.
  */

public final class ValidationGroups {
    private ValidationGroups() {}
    public interface Create {}
    public interface Update {}
    public interface Delete {}
    public interface Key {}
    public interface GroupKey {}
}
