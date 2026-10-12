package com.pagoflex.app.core.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import com.pagoflex.app.core.ui.theme.Dimens.radiusL
import com.pagoflex.app.core.ui.theme.Dimens.radiusM
import com.pagoflex.app.core.ui.theme.Dimens.radiusS
import com.pagoflex.app.core.ui.theme.Dimens.radiusXS
import com.pagoflex.app.core.ui.theme.Dimens.radiusXXL
import com.pagoflex.app.core.ui.theme.Dimens.radiusXXS

// ------ formas principales ------
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(radiusXXS),
    small = RoundedCornerShape(radiusXS),
    medium = RoundedCornerShape(radiusS),
    large = RoundedCornerShape(radiusM),
    extraLarge = RoundedCornerShape(radiusL)
)


// ------ formas extra ------
val CardShape = RoundedCornerShape(radiusS)

val ButtonShape = RoundedCornerShape(radiusXXL)

val IconButtonShape = CircleShape

val TextFieldShape = RoundedCornerShape(radiusS)