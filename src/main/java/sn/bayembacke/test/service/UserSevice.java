package sn.bayembacke.test.service;

import sn.bayembacke.test.be.Users;

import javax.xml.bind.ValidationException;

public interface UserSevice {

    public Users saveUser(Users user) throws ValidationException;
}
