package com.example.healthhub.dao;

import com.example.healthhub.model.Member;
import java.util.List;

public interface MemberDAO extends GenericDAO<Member> {
    Member findByUsername(String username);
    Member authenticate(String username, String password);
    List<Member> findByMemberType(String memberType);
    List<Member> searchByName(String name);
}