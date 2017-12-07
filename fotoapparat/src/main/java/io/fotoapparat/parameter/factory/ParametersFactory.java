package io.fotoapparat.parameter.factory;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Set;

import io.fotoapparat.hardware.Capabilities;
import io.fotoapparat.log.Logger;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.Parameters;
import io.fotoapparat.parameter.Size;
import io.fotoapparat.parameter.range.Range;
import io.fotoapparat.parameter.selector.SelectorFunction;

/**
 * Functions which build {@link Parameters} from given {@link Capabilities} and selector functions.
 */
public class ParametersFactory {

    /**
     * @return new parameters by selecting picture size from given capabilities.
     */
    public static Parameters selectPictureSize(@NonNull Capabilities capabilities,
                                               @NonNull SelectorFunction<Collection<Size>, Size> selector) {
        return new Parameters().putValue(
                Parameters.Type.PICTURE_SIZE,
                selectSafely(
                        selector,
                        capabilities.supportedPictureSizes()
                )
        );
    }

    /**
     * @return new parameters by selecting preview size from given capabilities.
     */
    public static Parameters selectPreviewSize(@NonNull Capabilities capabilities,
                                               @NonNull SelectorFunction<Collection<Size>, Size> selector) {
        return new Parameters().putValue(
                Parameters.Type.PREVIEW_SIZE,
                selectSafely(
                        selector,
                        capabilities.supportedPreviewSizes()
                )
        );
    }

    /**
     * @return new parameters by selecting focus mode from given capabilities.
     */
    public static Parameters selectFocusMode(@NonNull Capabilities capabilities,
                                             @NonNull SelectorFunction<Collection<FocusMode>, FocusMode> selector) {
        return new Parameters().putValue(
                Parameters.Type.FOCUS_MODE,
                selectSafely(
                        selector,
                        capabilities.supportedFocusModes()
                )
        );
    }

    /**
     * @return new parameters by selecting flash mode from given capabilities.
     */
    public static Parameters selectFlashMode(@NonNull Capabilities capabilities,
                                             @NonNull SelectorFunction<Collection<Flash>, Flash> selector) {
        return new Parameters().putValue(
                Parameters.Type.FLASH,
                selectSafely(
                        selector,
                        capabilities.supportedFlashModes()
                )
        );
    }

    /**
     * @return new parameters by selecting preview FPS range from given capabilities.
     */
    public static Parameters selectPreviewFpsRange(@NonNull Capabilities capabilities,
                                                   @NonNull SelectorFunction<Collection<Range<Integer>>, Range<Integer>> selector,
                                                   Logger logger) {
        Range<Integer> selectedFpsRange = selector.select(
                capabilities.supportedPreviewFpsRanges()
        );

        if (selectedFpsRange == null) {
            logger.log(
                    "No fps range has been selected. Will default to previously set one or default.");
            return new Parameters();
        }

        return new Parameters().putValue(
                Parameters.Type.PREVIEW_FPS_RANGE,
                selectedFpsRange
        );
    }

    /**
     * @return new parameters by selecting sensor sensitivity from given capabilities.
     */
    public static Parameters selectSensorSensitivity(@NonNull Capabilities capabilities,
                                                     @NonNull SelectorFunction<Range<Integer>, Integer> selector,
                                                     Logger logger) {
        Integer selectedSensorSensitivity = selector.select(
                capabilities.supportedSensorSensitivityRange()
        );

        if (selectedSensorSensitivity == null) {
            logger.log(
                    "No sensor sensitivity has been selected. Will default to previously set one or default.");
            return new Parameters();
        }

        return new Parameters().putValue(
                Parameters.Type.SENSOR_SENSITIVITY,
                selectedSensorSensitivity
        );
    }

    /**
     * @param jpegQuality integer (1-100)
     * @return new parameters with a set jpegQuality
     */
    public static Parameters selectJpegQuality(int jpegQuality) {
        return new Parameters().putValue(
                Parameters.Type.JPEG_QUALITY,
                ensureJpegQualityRange(jpegQuality)
        );
    }

    private static Integer ensureJpegQualityRange(int jpegQuality) {
        if (jpegQuality < 0 || 100 < jpegQuality) {
            throw new IllegalArgumentException("Jpeg quality was not in 0-100 range.");
        }
        return jpegQuality;
    }

    private static <T> T selectSafely(
            SelectorFunction<Collection<T>, T> selector,
            Set<T> capabilities
    ) {
        T selectedParameter = selector.select(capabilities);
        if (!capabilities.contains(selectedParameter)) {
            throw new IllegalArgumentException(
                    "The selected parameter is not in the supported set of values.");
        }
        return selectedParameter;
    }

}
