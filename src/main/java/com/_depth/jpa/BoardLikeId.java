package com._depth.jpa;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeId implements Serializable {
    private Long boardId;
    private Integer memberId;
}