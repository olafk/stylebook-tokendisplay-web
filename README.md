# stylebook-tokendisplay-web

## What is this?

This plugin generates a button in the fragment editor that gives you access
to either the Classic or Dialect themes' usable CSS tokens via their own sample portlets, so that you have a reference of the values
that you can use in your fragment's CSS. E.g. in the Dialect Theme, you may 
use `var(--color-brand-primary-lighten-2)` to address a color from the configured
stylebook

## How to build/use

Place this plugin in a Liferay Workspace's `modules` directory, build and deploy it.

This plugin has been developed with `liferay.workspace.product=dxp-7.4-u70`. 

It automatically creates a button in the ProductMenu of the Fragments Editor if either of the known themes is active in the current site's LayoutSet

## To Do

Requires manual updates when other themes come with their own sample widget 

## Screenshots

### Dialect Theme

![Dialect Sample](stylebook-tokendisplay-dialect-screenshot.png)

### Classic Theme

![Classic Sample](stylebook-tokendisplay-classic-screenshot.png)
