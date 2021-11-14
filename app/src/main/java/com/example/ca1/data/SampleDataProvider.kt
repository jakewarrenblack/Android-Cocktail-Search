package com.example.ca1.data

import java.util.*

class SampleDataProvider {
    companion object{
        private val sampleText1 = "Margarita"
        private val sampleText2 = "Banana daiquiri"
        private val sampleText3 = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque finibus, magna eget ullamcorper eleifend, neque justo cursus nibh, sit amet fermentum nisi dui sed justo. Nulla ac nisl ut nulla euismod mollis. Aenean ullamcorper eu odio a rutrum. Mauris eu augue tortor. Pellentesque erat justo, gravida sed maximus eu, faucibus at justo. Aliquam ut nulla consectetur odio vestibulum vulputate vel id est. Aliquam erat volutpat.

            Fusce maximus sagittis dolor in tempor. Duis vehicula congue lectus eu lobortis. Integer placerat fermentum sapien, vel feugiat sapien pellentesque non. Integer nec nibh sit amet ex lacinia pretium sed et eros. Nam id consequat erat, eleifend mollis quam. In dictum lobortis quam vel tincidunt. Vestibulum non lobortis neque. Phasellus pharetra malesuada mauris eget blandit. Sed ornare nisl id nisl tristique placerat.
        """.trimIndent()


        fun getCocktails() = arrayListOf(
            CocktailEntity(1, sampleText1),
            CocktailEntity(2, sampleText2),
            CocktailEntity(3, sampleText3)
        )

    }
}