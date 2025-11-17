package io.onelioh.babycut.model.timeline;

/**
 * Type d'un élément placé sur une timeline.
 *
 * CLIP : segment d'un MediaAsset (vidéo, audio ou image).
 * TRANSITION : effet de transition entre deux clips (à implémenter plus tard).
 */
public enum TimelineItemType {
    CLIP, TRANSITION
}
