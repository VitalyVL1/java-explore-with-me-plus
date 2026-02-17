package ru.practicum.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Реализация {@link Pageable} для поддержки пагинации на основе смещения (offset) и лимита (limit).
 * <p>
 * Стандартная реализация Spring Data JPA работает с номерами страниц (page number).
 * Данная реализация позволяет использовать пагинацию с явным указанием количества
 * пропускаемых элементов (offset), что соответствует требованиям API проекта.
 * </p>
 *
 * @see org.springframework.data.domain.PageRequest
 * @see Pageable
 */
@ToString
@EqualsAndHashCode
public class OffsetBasedPageable implements Pageable {

    private final int offset;
    private final int size;
    private final Sort sort;

    /**
     * Создает новый объект пагинации с указанным смещением, размером страницы и сортировкой.
     *
     * @param offset количество элементов для пропуска (должно быть неотрицательным)
     * @param size размер страницы (должен быть не менее 1)
     * @param sort параметры сортировки (если null, используется Sort.unsorted())
     * @throws IllegalArgumentException если offset меньше 0 или size меньше 1
     */
    public OffsetBasedPageable(int offset, int size, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero!");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.offset = offset;
        this.size = size;
        this.sort = sort == null ? Sort.unsorted() : sort;
    }

    /**
     * Создает новый объект пагинации с указанным смещением и размером страницы без сортировки.
     *
     * @param offset количество элементов для пропуска
     * @param size размер страницы
     */
    public OffsetBasedPageable(int offset, int size) {
        this(offset, size, Sort.unsorted());
    }

    /**
     * Возвращает номер страницы, вычисленный на основе offset и size.
     *
     * @return номер текущей страницы
     */
    @Override
    public int getPageNumber() {
        return offset / size;
    }

    /**
     * Возвращает размер страницы.
     *
     * @return количество элементов на странице
     */
    @Override
    public int getPageSize() {
        return size;
    }

    /**
     * Возвращает смещение для SQL запроса.
     *
     * @return количество элементов для пропуска
     */
    @Override
    public long getOffset() {
        return offset;
    }

    /**
     * Возвращает параметры сортировки.
     *
     * @return объект Sort
     */
    @Override
    public Sort getSort() {
        return sort;
    }

    /**
     * Создает объект пагинации для следующей страницы.
     *
     * @return новый объект OffsetBasedPageable со смещением offset + size
     */
    @Override
    public Pageable next() {
        return new OffsetBasedPageable(offset + size, size, sort);
    }

    /**
     * Создает объект пагинации для предыдущей страницы или первой, если предыдущей нет.
     *
     * @return объект пагинации для предыдущей или первой страницы
     */
    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    /**
     * Создает объект пагинации для первой страницы.
     *
     * @return объект пагинации с offset = 0
     */
    @Override
    public Pageable first() {
        return new OffsetBasedPageable(0, size, sort);
    }

    /**
     * Создает объект пагинации с указанным номером страницы.
     *
     * @param pageNumber номер страницы
     * @return объект пагинации со смещением pageNumber * size
     */
    @Override
    public Pageable withPage(int pageNumber) {
        int newOffset = pageNumber * size;
        return new OffsetBasedPageable(newOffset, size, sort);
    }

    /**
     * Проверяет, существует ли предыдущая страница.
     *
     * @return true если offset >= size, иначе false
     */
    @Override
    public boolean hasPrevious() {
        return offset >= size;
    }

    /**
     * Создает объект пагинации для предыдущей страницы.
     *
     * @return объект пагинации со смещением offset - size
     */
    public Pageable previous() {
        return hasPrevious() ? new OffsetBasedPageable(offset - size, size, sort) : this;
    }
}