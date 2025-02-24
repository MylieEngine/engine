Command Line Arguments Documentation
====================================

### `-allowFeatures`
Controls which features are available based on their status level.

- **Required Parameter**: One of the following status values:
    - `STABLE` - Only stable features (default)
    - `RELEASE_CANDIDATE` - Enables release candidate and stable features
    - `EXPERIMENTAL` - Enables all features including experimental ones

### `-allowDeprecation`
Enables the use of deprecated features that would otherwise be disabled.

- **Parameter**: None (flag-only argument)
- When present, allows the use of deprecated functionality
- When absent, deprecated features are disabled (default)
