package starter.kit.account;

import starter.kit.model.entity.Account;

/**
 * 账户登录接口，实现时应返回Account的实现类
 */
public interface AccountProvider {
  Account provideAccount(String accountJson);
}
