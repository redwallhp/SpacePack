# SpacePack
Fork of [AIOJetpacks](https://github.com/FreestyleCrafter/AIOJetpacks)

## Changes From AIOJetpacks

* Rewrote basic flight code and fuel handling, resulting in behavior more like Creative-mode flying.

* Added particle trails and sound

* Fuel ticks constantly instead of only when moving. If the player is flying, it will use one fuel unit per tick. If the player lands, it will use one fuel unit every twenty ticks. Disabling the jetpack stops fuel consumption.

* Jetpacks are now enabled and disabled with `/jp`, so players don't have immunity to fall damage all the time.

* WorldGuard no-fly regions
