import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';
import Link from '@docusaurus/Link';
import BannerImage from '@site/static/img/vintage.png';
import CurseForgeIcon from '@site/static/img/curseforge_icon.png';

const AddonDocumentationList = [
  {
    title: (
      <>
        Create an Addon <span class="badge badge--warning">Intermediate</span>
      </>
    ),
    link: '/docs/custom-backpack/introduction',
    description: (
      <>
        This guide will show you how to add even more backpacks to Backpacked using either a resource/data packs or by integrating into a mod. This guide will cover registering new backpacks, creating the models, and setting the challenges to unlock backpacks.
      </>
    ),
    action_text: 'View Guide'
  },
  {
    title: (
      <>
        Installing Addons <span class="badge badge--success">Easy</span>
      </>
    ),
    link: '#',
    description: (
      <>
        This guide will show you how to download and install addons for Backpacked
      </>
    ),
    action_text: 'Coming Soon'
  },
]

const Addons = [
  {
    name: 'Backpacked: World of Colours',
    author: 'Beast135',
    author_avatar: 'https://static-cdn.jtvnw.net/jtv_user_pictures/11d756f3-a687-489a-a229-c39757625059-profile_image-150x150.png',
    author_link: 'https://www.curseforge.com/members/beast135',
    description: 'Adds dye variants of the Standard Backpack',
    link: 'https://www.curseforge.com/minecraft/mc-mods/backpacked-world-of-color',
    icon: 'https://media.forgecdn.net/avatars/thumbnails/1005/64/64/64/638526407638986123_animated.gif',
  },
  {
    name: 'Backpacked: Shells',
    author: 'Beast135',
    author_avatar: 'https://static-cdn.jtvnw.net/jtv_user_pictures/11d756f3-a687-489a-a229-c39757625059-profile_image-150x150.png',
    author_link: 'https://www.curseforge.com/members/beast135',
    description: 'Adds 2 cool new shell backpacks to Backpacked!',
    link: 'https://www.curseforge.com/minecraft/mc-mods/backpacked-shells',
    icon: 'https://media.forgecdn.net/avatars/thumbnails/1637/383/64/64/639048018777295430.png',
  },
  {
    name: 'MrCrayfish\'s Furniture Mod: Refurbished',
    author: 'MrCrayfish',
    author_avatar: 'https://static-cdn.jtvnw.net/jtv_user_pictures/4ce607d6-66e9-45b8-a5ea-db8577519da2-profile_image-150x150.jpeg',
    author_link: 'https://www.curseforge.com/members/mrcrayfish',
    description: 'Adds an exclusive Storage Cabinet backpack when this mod is installed alongside Backpacked. ',
    link: 'https://www.curseforge.com/minecraft/mc-mods/refurbished-furniture',
    icon: 'https://media.forgecdn.net/avatars/thumbnails/934/904/64/64/638411339753659456.png',
  },
]

function Documentation({ title, description, link, action_text }) {
  return (
    <div className={clsx('col col--6 margin-bottom--lg')}>
      <div class="card shadow--lw fill-height">
        <div class="card__header">
          <h3>{title}</h3>
          <p>{description}</p>
        </div>
        <div class="card__footer">
          <Link
            className="button button--secondary button--lg button--block"
            to={link}>
            {action_text}
          </Link>
        </div>
      </div>
    </div>
  );
}

function Addon({ name, link, author, author_avatar, author_link, description, icon }) {
  return (
    <div className={clsx('col col--12 margin-bottom--lg')}>
      <div class="card shadow--lw">
        <div class="card__body">
          <div class="container">
            <div class="row">
              <img src={icon} width={100} height={100}/>
              <div class="margin-left--md">
                <div class="addon-header">
                  <h3 class="margin-bottom--sm">{name}</h3>
                  <div class="avatar avatar-author avatar--xs margin-left--sm">
                    <img class="avatar__photo avatar__photo--xs" src={author_avatar} />
                    <div class="avatar__intro">
                      <div class="avatar__name"><a href={author_link}>{author}</a></div>
                    </div>
                  </div>
                </div>
                <p class="margin-bottom--sm">{description}</p>
                <a class="button curseforge" href={link} target="_blank"><img src={CurseForgeIcon} height={12}/> CurseForge</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <div>
      <div className={clsx('hero hero--backpacked how-to-center-a-div')}>
        <img src={BannerImage} width={250} height={250} />
      </div>
      <section className={styles.features}>
        <div className="container">
          <Heading as="h1">📙 Guides</Heading>
          <div className="row row--align-stretch">
            {AddonDocumentationList.map((props, idx) => (
              <Documentation key={idx} {...props} />
            ))}
          </div>
          <Heading as="h1">🏘️ Community Addons</Heading>
          <div className="row row--align-stretch">
            {Addons.map((props, idx) => (
              <Addon key={idx} {...props} />
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}