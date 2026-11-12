# Pattern-match the probcliVersion string value out of the build.gradle.
# For use by the .gitlab-ci.yml release deploy job.
# This is not a good solution, but it works :(

/^def probcliVersion = / {
	print substr($0, index($0, "\"") + 1, length($0) - index($0, "\"") - 1);
}
