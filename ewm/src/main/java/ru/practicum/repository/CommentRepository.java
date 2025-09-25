package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.user.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor(User author);

    @Query("SELECT NEW ru.practicum.dto.comment.StateCommentDto(" +
            "c.id, " +
            "c.author.name, " +
            "c.text, " +
            "c.state) " +
            "FROM Comment c " +
            "WHERE (:text IS NULL OR :text = '' OR LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'DESC' THEN c.created END DESC, " +
            "CASE WHEN :sort = 'ASC' OR :sort IS NULL THEN c.created END ASC")
    List<StateCommentDto> findCommentsWithSearchAndSort(@Param("text") String text,
                                                        @Param("sort") String sort);
}
