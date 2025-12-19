package toy.com.common.egovUserSession.service;

public interface EgovUserSessionService {
    Object getAuthenticatedAdmin();

    //Object getAuthenticatedUser();

    Object getAuthenticatedNoMbr();

    Boolean isAuthenticated();

    Boolean isAuthenticatedNoMbr();

    Boolean isAdminAuthenticated();

    String getAdminUid();

    //String getUserUid();
}
