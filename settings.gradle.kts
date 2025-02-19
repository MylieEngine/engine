rootProject.name = "mylie.engine"

include("utils")
project(":utils").projectDir = File(rootDir, "engine/utils")

include("core")
project(":core").projectDir = File(rootDir, "engine/core")

include("platform.desktop")
project(":platform.desktop").projectDir = File(rootDir, "engine/platform/desktop")