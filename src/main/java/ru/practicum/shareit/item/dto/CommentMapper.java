package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    Comment toComment(CommentDto CommentDto);

    @Mapping(target = "authorName", expression = "java(comment.getUser().getName())")
    CommentDto toCommentDto(Comment comment);
}
