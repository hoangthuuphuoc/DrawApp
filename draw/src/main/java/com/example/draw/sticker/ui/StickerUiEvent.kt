sealed interface StickerUiEvent {

    data object AddTextClicked : StickerUiEvent

    data class ConfirmTextClicked(
        val text: String
    ) : StickerUiEvent

    data object AddBitmapClicked : StickerUiEvent

    data object TextColorClicked : StickerUiEvent

    data class TextColorSelected(
        val color: Int
    ) : StickerUiEvent

    data object TextSizeClicked : StickerUiEvent

    data class TextSizeSelected(
        val size: Float
    ) : StickerUiEvent

    data object TextStrokeClicked : StickerUiEvent

    data class TextStrokeWidthSelected(
        val width: Float
    ) : StickerUiEvent

    data class TextStrokeColorSelected(
        val color: Int
    ) : StickerUiEvent

    data object TextFontClicked : StickerUiEvent

    data class TextFontSelected(
        val font: Int
    ) : StickerUiEvent

    data class StickerCategoryClicked(
        val category: String
    ) : StickerUiEvent

    data class StickerSelected(
        val image: Int
    ) : StickerUiEvent

    data object CropClicked : StickerUiEvent

    data class CropRatioSelected(
        val width: Int,
        val height: Int,
        val name: String
    ) : StickerUiEvent
}