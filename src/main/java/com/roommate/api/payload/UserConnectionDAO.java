package com.roommate.api.payload;

import com.roommate.api.model.UserConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class UserConnectionDAO {

	@Autowired
	private EntityManager entityManager;

	public UserConnection findUserConnectionByUserProviderId(String userProviderId) {
		try {
			String sql = "Select e from " + UserConnection.class.getName() + " e " //
					+ " Where e.providerUserId = :providerUserId ";

			Query query = entityManager.createQuery(sql, UserConnection.class);
			query.setParameter("providerUserId", userProviderId);

			@SuppressWarnings("unchecked")
			List<UserConnection> list = query.getResultList();

			return list.isEmpty() ? null : list.get(0);
		} catch (NoResultException e) {
			return null;
		}
	}
}
