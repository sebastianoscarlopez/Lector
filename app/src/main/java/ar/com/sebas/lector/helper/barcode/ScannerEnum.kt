package ar.com.sebas.lector.helper.barcode

/**
 * Tipos de scanner a utilizar
 *
 * EMDK: Lector laser
 * CAMERA: Lector con Google Vision
 * DEFAULT: Intenta crear EMDK si falla crea con CAMERA
 */
enum class ScannerEnum {
    DEFAULT,
    EMDK,
    CAMERA
}
