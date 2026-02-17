package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentState;
import ru.practicum.model.comment.QComment;
import ru.practicum.model.user.User;

import java.util.List;

/**
 * Репозиторий для выполнения операций с сущностью {@link Comment}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также методы для поиска комментариев по автору и динамической фильтрации
 * с использованием QueryDSL {@link QuerydslPredicateExecutor}.
 * </p>
 *
 * @see Comment
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 * @see QComment
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    /**
     * Находит все комментарии указанного автора.
     *
     * @param author пользователь - автор комментариев
     * @return список комментариев автора
     */
    List<Comment> findAllByAuthor(User author);

    /**
     * Внутренний интерфейс, содержащий фабричные методы для создания предикатов QueryDSL.
     * <p>
     * Используется для динамического построения условий поиска комментариев.
     * </p>
     */
    interface Predicate {

        /**
         * Создает предикат для фильтрации комментариев по тексту (регистронезависимый поиск).
         *
         * @param text текст для поиска (если null или пустой, предикат не добавляет условий)
         * @return BooleanBuilder с условием поиска по тексту или пустой предикат
         */
        static BooleanBuilder textFilter(String text) {
            BooleanBuilder predicate = new BooleanBuilder();
            QComment comment = QComment.comment;

            if (text != null && !text.isBlank()) {
                String searchText = text.trim();
                predicate.and(comment.text.lower().contains(searchText.toLowerCase()));
            }

            return predicate;
        }

        /**
         * Создает предикат для фильтрации комментариев по статусу.
         *
         * @param state статус комментария {@link CommentState}
         * @return BooleanBuilder с условием равенства статуса
         */
        static BooleanBuilder stateFilter(CommentState state) {
            BooleanBuilder predicate = new BooleanBuilder();
            QComment comment = QComment.comment;
            predicate.and(comment.state.eq(state));
            return predicate;
        }

        /**
         * Создает предикат для фильтрации комментариев по идентификатору события.
         *
         * @param eventId идентификатор события
         * @return BooleanBuilder с условием принадлежности комментария событию
         */
        static BooleanBuilder eventFilter(long eventId) {
            BooleanBuilder predicate = new BooleanBuilder();
            QComment comment = QComment.comment;
            predicate.and(comment.event.id.eq(eventId));
            return predicate;
        }
    }
}