package com.example.treasurehunter.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Play_arrow: ImageVector
	get() {
		if (_Play_arrow != null) {
			return _Play_arrow!!
		}
		_Play_arrow = ImageVector.Builder(
            name = "Play_arrow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(320f, 760f)
				verticalLineToRelative(-560f)
				lineToRelative(440f, 280f)
				close()
				moveToRelative(80f, -146f)
				lineToRelative(210f, -134f)
				lineToRelative(-210f, -134f)
				close()
			}
		}.build()
		return _Play_arrow!!
	}

private var _Play_arrow: ImageVector? = null
