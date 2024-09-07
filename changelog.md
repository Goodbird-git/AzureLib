v2.3.18

- Adds hardStop() for nulling path and pathToPosition if you need it to stop before reaching end node in path in AzureNavigation.
- Made pathToPosition protected instead of private in AzureNavigation.
- Adds error for new bedrock format number.
- Fixes crash due to null context on getPathType in AzureNavigation.
- Deprecated DyeableGeoArmorRenderer for 1.21+
- Update everything to properly use the colour value, this is a required breaking change.
- Tweak AutoGlowingTexture