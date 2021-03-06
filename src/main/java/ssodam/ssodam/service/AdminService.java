package ssodam.ssodam.service;

import ssodam.ssodam.domain.Category;

public interface AdminService {
    /* Category 생성 및 삭제 */
    Long createCategory(Category category);

    /* Member 차단 */
    void blockMember(Long memberId);

    /* Member 로그인 체크 */
    boolean loginCheck(Long memberId);
}
