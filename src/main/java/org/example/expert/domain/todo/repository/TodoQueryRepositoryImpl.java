package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;

@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findTodoByWithUser(Long todoId) {
        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin() // User와 fetchJoin
                .where(todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(result);

    }

    @Override
    public Page<TodoSearchResponse> searchTodos(String title, LocalDateTime startDate, LocalDateTime endDate, String managerNickname, Pageable pageable) {
        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(title),
                        createdAtBetween(startDate, endDate),
                        managerNicknameContains(managerNickname)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.id.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(
                        titleContains(title),
                        createdAtBetween(startDate, endDate),
                        managerNicknameContains(managerNickname)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? todo.title.contains(title) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        }
        if (startDate != null) {
            return todo.createdAt.goe(startDate);
        }
        if (endDate != null) {
            return todo.createdAt.loe(endDate);
        }
        return null;
    }

    private BooleanExpression managerNicknameContains(String managerNickname) {
        return StringUtils.hasText(managerNickname) ? manager.user.nickname.contains(managerNickname) : null;
    }
}
